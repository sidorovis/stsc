 * Remember that:

Please run update liquibase command before starting tests or developer cycle.

liquibase :

mvn liquibase:update -Pdeveloper && mvn resources:resources liquibase:update -Ptest && mvn resources:resources liquibase:update -Pproduction
mvn resources:resources liquibase:rollback -Dliquibase.rollbackCount=1 
mvn resources:resources liquibase:dropAll

