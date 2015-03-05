 * Remember that:

Please run update liquibase command before starting tests or developer cycle.

liquibase :

mvn resources:resources liquibase:update -Pyahoo.test
mvn resources:resources liquibase:update -Pyahoo.development
mvn resources:resources liquibase:update -Pyahoo.production

mvn resources:resources liquibase:update -Pfeedzilla.test
mvn resources:resources liquibase:update -Pfeedzilla.development
mvn resources:resources liquibase:update -Pfeedzilla.peoduction

mvn resources:resources liquibase:rollback -Dliquibase.rollbackCount=1 
mvn resources:resources liquibase:dropAll

