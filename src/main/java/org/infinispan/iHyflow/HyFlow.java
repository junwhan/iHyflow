package org.infinispan.iHyflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.deuce.transform.Exclude;

import aleph.dir.DirectoryManager;
import org.infinispan.iHyflow.core.AbstractDistinguishable;
import org.infinispan.iHyflow.core.cm.policy.AbstractContentionPolicy;
import org.infinispan.iHyflow.helper.TrackerLockMap;
import org.infinispan.iHyflow.util.network.Network;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.ConfigurationChildBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationChildBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jetbrains.annotations.NotNull;

@Exclude
public class HyFlow {
   	private static EmbeddedCacheManager m_cacheManager = null;
   	private static Cache<Object, AbstractDistinguishable> m_cache = null;
	public static <T extends AbstractDistinguishable> DirectoryManager getLocator() {
		return DirectoryManager.getManager();
	}
    @NotNull
   public static Cache<Object, AbstractDistinguishable> getMyObjectCache (String NodeName, String CacheName) throws IOException
   {
      if( m_cache == null ){
         //createMyObjectCache();
    	
          DefaultCacheManager cacheManager = new DefaultCacheManager(
                  GlobalConfigurationBuilder.defaultClusteredBuilder()
                        .transport().nodeName(NodeName).addProperty("configurationFile", "jgroups.xml")
                        .build(),
                  new ConfigurationBuilder()
                        .clustering()
                        .cacheMode(CacheMode.LOCAL)
                        .build()
            );
            cacheManager.defineConfiguration("local", new ConfigurationBuilder()
                  .clustering()
                  .cacheMode(CacheMode.LOCAL)
                  .hash().numOwners(Network.getInstance().nodesCount())
                  .build()
            );
  	  
    	  //DefaultCacheManager cacheManager = new DefaultCacheManager("infinispan.xml");
    	  m_cache = cacheManager.getCache(CacheName);
      }
      return m_cache;
   }

   @NotNull
   public static AbstractDistinguishable getMyObject(Object id)
   {
	   return m_cache.get(id);
	   
   }
   @NotNull
   public static void putMyObject(Object id, AbstractDistinguishable obj)
   {
	   m_cache.put(id, obj);
	   
   }
   @NotNull
   public static void removeMyObject(Object id)
   {
	   m_cache.remove(id);
	   
   }
   private static synchronized void createMyObjectCache ()
   {
      if( m_cacheManager == null )
         createCacheManager();

      if( !m_cacheManager.cacheExists( "MyObjects" ) )
      {
         Configuration cacheConfig = createMyObjectCacheConfig();
         m_cacheManager.defineConfiguration( "MyObjects", cacheConfig );
      }

      if( !m_cacheManager.isRunning( "MyObjects" ) )
         m_cacheManager.getCache( "MyObjects" ).start();
      m_cache = m_cacheManager.getCache( "MyObjects" );
   }
   private static void createCacheManager ()
   {
      GlobalConfiguration globalConfiguration = createGlobalConfiguration();
      Configuration defaultCacheConfig = createDefaultCacheConfiguration();
      m_cacheManager = new DefaultCacheManager( globalConfiguration, defaultCacheConfig );
      
   }

   @NotNull
   private static Configuration createDefaultCacheConfiguration ()
   {
      ConfigurationChildBuilder builder = new ConfigurationBuilder();
      builder = builder.jmxStatistics().enable();
      builder = builder.clustering()
         .cacheMode( CacheMode.DIST_SYNC )
         .stateTransfer()
         .timeout( 100 );
      builder = builder.transaction()
         .transactionMode( TransactionMode.TRANSACTIONAL )
         .autoCommit( false )
         .lockingMode( LockingMode.OPTIMISTIC );
      return builder.build();
   }

   @NotNull
   private static GlobalConfiguration createGlobalConfiguration ()
   {
      GlobalConfigurationChildBuilder builder = new GlobalConfigurationBuilder().clusteredDefault();
      builder = builder.globalJmxStatistics()
         .enable()
         .cacheManagerName( "MyCacheManager" )
         .jmxDomain( "com.iHyflow.cache" );
      return builder.build();
   }

   @NotNull
   private static Configuration createMyObjectCacheConfig ()
   {
      ConfigurationChildBuilder builder = new ConfigurationBuilder();
      builder = builder.jmxStatistics().enable();
      builder = builder.clustering().cacheMode( CacheMode.DIST_SYNC );
      builder = builder.transaction()
         .transactionMode( TransactionMode.TRANSACTIONAL )
         .autoCommit( false )
         .lockingMode( LockingMode.OPTIMISTIC )
         .transactionManagerLookup( new GenericTransactionManagerLookup() );
      builder = builder.locking().isolationLevel( IsolationLevel.REPEATABLE_READ );
      builder = builder.eviction().maxEntries( 100 ).strategy( EvictionStrategy.LRU );
      builder = builder.expiration().maxIdle( 30000 ).lifespan( -1 ).enableReaper();
      builder = builder.loaders().passivation( true ).addFileCacheStore().purgeOnStartup( true );
      builder = builder.indexing().enable().addProperty( "default.directory_provider", "ram" );
      return builder.build();
   }
   
	//public static Map<String, requester> commutativity = new ConcurrentHashMap<String, requester>();
	
	private static AbstractContentionPolicy contentionManager;
	public static AbstractContentionPolicy getConflictManager() {
		if (contentionManager == null)
			synchronized (HyFlow.class) {
				if (contentionManager == null)
					try {
						contentionManager = (AbstractContentionPolicy) Class
								.forName(System.getProperty("contentionPolicy"))
								.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			}
		return contentionManager;
	}

	public static void readConfigurations() throws FileNotFoundException,
			IOException {
		Properties defaults = new Properties();
		defaults.load(HyFlow.class.getResourceAsStream("default.conf"));

		// rename Aleph pros.
		System.setProperty("aleph.directoryManager", System.getProperty(
				"directoryManager", defaults.remove("directoryManager")
						.toString()));
		System.setProperty("aleph.communicationManager", System.getProperty(
				"communicationManager", defaults.remove("communicationManager")
						.toString()));

		// rename Deuce pros.
		System.setProperty("org.deuce.transaction.contextClass", System
				.getProperty("context", defaults.get("context").toString()));

		// load HyFlow pros.
		for (Object key : defaults.keySet())
			System.setProperty(key.toString(), System.getProperty(key
					.toString(), defaults.getProperty(key.toString())));
	}

	public static void start(int id) {
		// Init Network with node info //
		Network.init(id);
		try {
			getMyObjectCache (((Integer)id).toString(), "MyObject");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// Warm-up //
		// TODO: dynamic loading of remote classes
		// getRemoteCaller(BankAccount.class);
	}
}
