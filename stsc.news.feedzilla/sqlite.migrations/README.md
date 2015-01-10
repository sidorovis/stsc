 * Remember that:

Please run update liquibase command before starting tests or developer cycle.

liquibase :

mvn liquibase:update -Pdeveloper && mvn liquibase:update -Ptest
mvn liquibase:rollback -Dliquibase.rollbackCount=1 
mvn liquibase:dropAll

