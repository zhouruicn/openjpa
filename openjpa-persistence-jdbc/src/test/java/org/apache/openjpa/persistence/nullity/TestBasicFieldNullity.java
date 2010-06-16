/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.persistence.nullity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.persistence.InvalidStateException;
import org.apache.openjpa.persistence.OpenJPAPersistence;

/**
 * Test @Basic(optional=true|false) and @Column(nullable=true|false) 
 * specification is honored. 
 * Note: null constraint violation manifests as different exception types
 * for option and nullable condition.
 *
 * @author Pinaki Poddar
 */
public class TestBasicFieldNullity extends AbstractNullityTestCase {

    public void setUp() {
        setUp(CLEAR_TABLES, RETAIN_DATA, NullValues.class);
    }

    public void testNullOnOptionalFieldIsAllowed() {
    	NullValues pc = new NullValues();
    	pc.setOptional(null); 
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnNonOptionalFieldIsDisallowed() {
    	NullValues pc = new NullValues();
    	pc.setNotOptional(null);
    	assertCommitFails(pc, NEW, InvalidStateException.class);
    }
    
    public void testNotNullOnOptionalFieldIsAllowed() {
    	NullValues pc = new NullValues();
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNotNullOnNonOptionalFieldIsAllowed() {
    	NullValues pc = new NullValues();
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnNullableColumnAllowed() {
    	NullValues pc = new NullValues();
    	pc.setNullable(null);
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnNonNullableColumnIsDisallowed() {
    	NullValues pc = new NullValues();
    	pc.setNotNullable(null);
    	assertCommitFails(pc, NEW, RollbackException.class);
    }
    
    public void testNotNullOnNullableColumnIsAllowed() {
    	NullValues pc = new NullValues();
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNotNullOnNonNullableColumnIsAllowed() {
    	NullValues pc = new NullValues();
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnOptionalBlobFieldIsAllowed() {
    	NullValues pc = new NullValues();
    	pc.setOptionalBlob(null);
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnNonOptionalBlobFieldIsDisallowed() {
    	NullValues pc = new NullValues();
    	pc.setNotOptionalBlob(null);
    	assertCommitFails(pc, NEW, InvalidStateException.class);
    }
    
    public void testNullOnNullableBlobColumnAllowed() {
    	NullValues pc = new NullValues();
    	pc.setNullableBlob(null);
    	assertCommitSucceeds(pc, NEW);
    }
    
    public void testNullOnNonNullableBlobColumnIsDisallowed() {
    	NullValues pc = new NullValues();
    	pc.setNotNullableBlob(null);
    	assertCommitFails(pc, NEW, RollbackException.class);
    }
    
    public void testX() {
    	NullValues pc = new NullValues();
    	assertCommitSucceeds(pc, NEW);
    	OpenJPAPersistence.getEntityManager(pc).close();
    	
    	pc.setNotNullableBlob(null);
    	assertCommitFails(pc, !NEW, RollbackException.class);
    }
    
    
    public void testUniqueStringColumnCanBeNull() {
        if (!isUniqueColumnNullable()) {
            return;
        }
        NullValues pc = new NullValues();
        pc.setUniqueNullable(null);
        assertCommitSucceeds(pc, NEW);
    }
    
    public void testUniqueStringColumnAsNull() {
        if (!isUniqueColumnNullable()) {
            return;
        }
        NullValues pc = new NullValues();
        pc.setUniqueNullable(null);
        assertCommitSucceeds(pc, NEW);
        
        String jpql = "select n from NullValues n where n.uniqueNullable = :p";
        EntityManager em = emf.createEntityManager();
        List<NullValues> result = em.createQuery(jpql, NullValues.class)
                                    .setParameter("p", null)
                                    .getResultList();
        assertFalse(result.isEmpty());
        for (NullValues n : result)
            assertNull(n.getUniqueNullable());
    }
    
    public void testUniqueStringColumnAsEmpty() {
        String EMPTY_STRING = "";
        NullValues pc = new NullValues();
        pc.setUniqueNullable(EMPTY_STRING);
        assertCommitSucceeds(pc, NEW);
        
        String jpql = "select n from NullValues n where n.uniqueNullable = :p";
        EntityManager em = emf.createEntityManager();
        List<NullValues> result = em.createQuery(jpql, NullValues.class)
                                    .setParameter("p", EMPTY_STRING)
                                    .getResultList();
        assertFalse(result.isEmpty());
        for (NullValues n : result)
            assertEquals(EMPTY_STRING, n.getUniqueNullable());
        
    }
    
    boolean isUniqueColumnNullable() {
        return ((JDBCConfiguration)emf.getConfiguration()).getDBDictionaryInstance().supportsNullUniqueColumn;
    }
}
