/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.hibernate4.access;

import com.hazelcast.hibernate4.region.HazelcastRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;

import java.util.Properties;

/**
 * Makes <b>READ COMMITTED</b> consistency guarantees even in a clustered environment.
 *
 * @author Leo Kim (lkim@limewire.com)
 */
public class ReadWriteAccessDelegate<T extends HazelcastRegion> extends AbstractAccessDelegate<T> {


    public ReadWriteAccessDelegate(T hazelcastRegion, final Properties props) {
        super(hazelcastRegion, props);
    }

    public boolean afterInsert(final Object key, final Object value, final Object version) throws CacheException {
        return put(key, value, version);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Called after <code>com.hazelcast.ReadWriteAccessDelegate.lockItem()</code>
     */
    public boolean afterUpdate(final Object key, final Object value, final Object currentVersion, final Object previousVersion,
                               final SoftLock lock) throws CacheException {
        try {
            return update(key, value, currentVersion, previousVersion, lock);
        } finally {
            unlockItem(key, lock);
        }
    }

    public boolean putFromLoad(final Object key, final Object value, final long txTimestamp, final Object version,
                               final boolean minimalPutOverride) throws CacheException {
        return put(key, value, version);
    }

    public SoftLock lockItem(final Object key, final Object version) throws CacheException {
        return cache.tryLock(key, version);
    }

    public void unlockItem(final Object key, final SoftLock lock) throws CacheException {
        cache.unlock(key, lock);
    }

    public void unlockRegion(SoftLock lock) throws CacheException {
    }
}
