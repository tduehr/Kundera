/**
 * Copyright 2012 Impetus Infotech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.client.neo4j.imdb;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for validating transaction handling provided by Kundera for Neo4J
 * 
 * @author amresh.singh
 */
public class IMDBTransactionTest
{

    EntityManagerFactory emf;

    EntityManager em;

    Actor actor1;

    Actor actor2;
    
    private static final String IMDB_PU = "imdb";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {        
        emf = Persistence.createEntityManagerFactory(IMDB_PU);
        em = emf.createEntityManager();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {            
        
        em.getTransaction().begin();
        em.remove(actor1);
        em.remove(actor2);
        em.getTransaction().commit();

        em.close();
        emf.close();     
    }  
    

    @Test
    public void withTransaction()
    {
        try
        {
            /** Prepare data */
            // Actors
            prepareData();

            /** Insert records */
            em.getTransaction().begin();
            em.persist(actor1);
            em.persist(actor2);
            em.getTransaction().commit();

            /** Find records (Doesn't need transaction) */
            Actor actor11 = em.find(Actor.class, 1);
            Actor actor22 = em.find(Actor.class, 2);
            assertActors(actor11, actor22);

            /** Update records */
            em.clear();
            actor1.setName("Amresh");
            actor2.setName("Amir");
            em.getTransaction().begin();
            em.merge(actor1);
            em.merge(actor2);
            em.getTransaction().commit();
            em.clear();
            Actor actor1AfterMerge = em.find(Actor.class, 1);
            Actor actor2AfterMerge = em.find(Actor.class, 2);
            assertUpdatedActors(actor1AfterMerge, actor2AfterMerge);

            /** Delete records */
            em.clear();
            em.getTransaction().begin();
            em.remove(actor11);
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.remove(actor22);
            em.getTransaction().commit();
            em.clear(); // clear cache
            Actor actor1AfterDeletion = em.find(Actor.class, 1);
            Actor actor2AfterDeletion = em.find(Actor.class, 2);
            Assert.assertNull(actor1AfterDeletion);
            Assert.assertNull(actor2AfterDeletion);

        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void withoutTransaction()
    {
        /** Prepare data */
        prepareData();

        /** Insert records without a transaction */
        try
        {
            em.persist(actor1);
            em.persist(actor2);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().toString().indexOf("transaction") >= 0);
        }

        /** Find records */
        em.clear();
        Actor actor11 = em.find(Actor.class, 1);
        Actor actor22 = em.find(Actor.class, 2);
        Assert.assertNull(actor11);
        Assert.assertNull(actor22);

    }

    @Test
    public void rollbackBehavior()
    {
        prepareData();

        em.getTransaction().begin();
        em.persist(actor1);
        em.persist(actor2);
        em.getTransaction().rollback();

        em.clear();
        Actor actor11 = em.find(Actor.class, 1);
        Actor actor22 = em.find(Actor.class, 2);
        Assert.assertNull(actor11);
        Assert.assertNull(actor22);
    }

    @Test
    public void rollbackBehaviorOnException()
    {
        prepareData();

        try
        {
            em.getTransaction().begin();
            em.persist(actor1);
            em.persist(actor2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            actor1.setName("Amresh");
            actor2.setName("Amir");
            em.merge(actor1);
            em.merge(actor2);
            em.merge(null);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            em.clear();
            Actor actor11 = em.find(Actor.class, 1);
            Actor actor22 = em.find(Actor.class, 2);
            Assert.assertNotNull(actor11);
            Assert.assertNotNull(actor22);
            Assert.assertNotSame("Amresh", actor11.getName());
            Assert.assertNotSame("Amir", actor22.getName());

        }

    }

    /**
     * @param actor1
     * @param actor2
     */
    private void assertActors(Actor actor1, Actor actor2)
    {
        Assert.assertNotNull(actor1);
        Assert.assertEquals(1, actor1.getId());
        Assert.assertEquals("Tom Cruise", actor1.getName());
        Map<Role, Movie> movies1 = actor1.getMovies();
        Assert.assertFalse(movies1 == null || movies1.isEmpty());
        Assert.assertEquals(2, movies1.size());

        Assert.assertNotNull(actor2);
        Assert.assertEquals(2, actor2.getId());
        Assert.assertEquals("Emmanuelle Béart", actor2.getName());
        Map<Role, Movie> movies2 = actor2.getMovies();
        Assert.assertFalse(movies2 == null || movies2.isEmpty());
        Assert.assertEquals(2, movies2.size());
    }

    /**
     * @param actor1
     * @param actor2
     */
    private void assertUpdatedActors(Actor actor1, Actor actor2)
    {
        Assert.assertNotNull(actor1);
        Assert.assertEquals(1, actor1.getId());
        Assert.assertEquals("Amresh", actor1.getName());
        Map<Role, Movie> movies1 = actor1.getMovies();
        Assert.assertFalse(movies1 == null || movies1.isEmpty());
        Assert.assertEquals(2, movies1.size());

        Assert.assertNotNull(actor2);
        Assert.assertEquals(2, actor2.getId());
        Assert.assertEquals("Amir", actor2.getName());
        Map<Role, Movie> movies2 = actor2.getMovies();
        Assert.assertFalse(movies2 == null || movies2.isEmpty());
        Assert.assertEquals(2, movies2.size());
    }

    private void prepareData()
    {
        // Actors
        actor1 = new Actor(1, "Tom Cruise");
        actor2 = new Actor(2, "Emmanuelle Béart");

        // Movies
        Movie movie1 = new Movie("m1", "War of the Worlds", 2005);
        Movie movie2 = new Movie("m2", "Mission Impossible", 1996);
        Movie movie3 = new Movie("m3", "Hell", 2005);

        // Roles
        Role role1 = new Role("Ray Ferrier", "Lead Actor");
        role1.setActor(actor1);
        role1.setMovie(movie1);
        Role role2 = new Role("Ethan Hunt", "Lead Actor");
        role2.setActor(actor1);
        role2.setMovie(movie2);
        Role role3 = new Role("Claire Phelps", "Lead Actress");
        role3.setActor(actor2);
        role1.setMovie(movie2);
        Role role4 = new Role("Sophie", "Supporting Actress");
        role4.setActor(actor2);
        role1.setMovie(movie3);

        // Relationships
        actor1.addMovie(role1, movie1);
        actor1.addMovie(role2, movie2);
        actor2.addMovie(role3, movie2);
        actor2.addMovie(role4, movie3);

        movie1.addActor(role1, actor1);
        movie2.addActor(role2, actor1);
        movie2.addActor(role3, actor2);
        movie3.addActor(role4, actor2);
    }

}
