package com.edgareldy.jsftutorial.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Test-only substitute for {@code config.EntityManagerProducer}: produces a
 * single {@code @ApplicationScoped} {@link EntityManager} shared by every DAO
 * in the test run, instead of {@code @RequestScoped}. Arquillian's
 * Weld-SE-embedded adapter has no HTTP request to activate that context
 * automatically, and a plain {@code @Dependent} producer would hand each DAO
 * its own EntityManager/persistence context, which breaks any test spanning
 * more than one DAO (e.g. saving a Product referencing a Category persisted
 * through a different DAO). One shared EntityManager mirrors what a single
 * HTTP request actually gets in production.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class TestEntityManagerProducer {

    private EntityManagerFactory entityManagerFactory;

    @Produces
    @ApplicationScoped
    public EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("jsf_tutorialPU");
        }
        return entityManagerFactory;
    }
}
