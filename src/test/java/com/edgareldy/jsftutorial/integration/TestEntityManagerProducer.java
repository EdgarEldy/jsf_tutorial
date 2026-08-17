package com.edgareldy.jsftutorial.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Test-only substitute for {@code config.EntityManagerProducer}: produces a
 * plain {@code @Dependent} (unscoped) {@link EntityManager} instead of
 * {@code @RequestScoped}, since Arquillian's Weld-SE-embedded adapter has no
 * HTTP request to activate that context automatically. Fine for a single-
 * threaded JUnit run; the real request-scoping only matters under concurrent
 * Tomcat requests, which this DAO-level test isn't exercising.
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
