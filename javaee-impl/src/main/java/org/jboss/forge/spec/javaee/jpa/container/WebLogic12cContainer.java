package org.jboss.forge.spec.javaee.jpa.container;

import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

public class WebLogic12cContainer extends CustomJTAContainer {

    @Override
    public PersistenceUnitDef setupConnection(PersistenceUnitDef unit, JPADataSource dataSource) {

        if ("org.hibernate.ejb.HibernatePersistence".equals(unit.getProvider())) {
            unit.property("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform");
        }

        return super.setupConnection(unit, dataSource);
    }

}
