/*******************************************************************************
 * * Copyright 2013 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.kundera.metadata.model;

import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.entities.Article;

/**
 * @author amresh.singh
 *
 */
public class ApplicationMetadataTest
{

    private String persistenceUnit = "patest";
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        Persistence.createEntityManagerFactory(persistenceUnit);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        KunderaMetadata.INSTANCE.getApplicationMetadata().unloadApplicationMatadata(persistenceUnit);
        KunderaMetadata.INSTANCE.setApplicationMetadata(null);
    }

    /**
     * Test method for {@link com.impetus.kundera.metadata.model.ApplicationMetadata#addEntityMetadata(java.lang.String, java.lang.Class, com.impetus.kundera.metadata.model.EntityMetadata)}.
     */
    @Test
    public void testAddEntityMetadata()
    {
        KunderaMetadata.INSTANCE.getApplicationMetadata().addEntityMetadata(persistenceUnit, Article.class, new EntityMetadata(Article.class));
        Assert.assertNotNull(KunderaMetadataManager.getEntityMetadata(Article.class));
    } 

}
