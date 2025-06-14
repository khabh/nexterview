package com.nexterview.server.util;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private static final String DISABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 0";
    private static final String ENABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 1";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE ";
    private static final String RESET_AUTO_INCREMENT = "ALTER TABLE %s AUTO_INCREMENT = 1";

    private final List<String> tableNames;
    private final EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
        List<Object> results = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        this.tableNames = results.stream()
                .map(table -> (String) table)
                .toList();
    }

    @Transactional
    public void clear() {
        entityManager.createNativeQuery(DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(TRUNCATE_TABLE + tableName).executeUpdate();
            entityManager.createNativeQuery(String.format(RESET_AUTO_INCREMENT, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        entityManager.clear();
    }
}
