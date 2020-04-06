# align-environment-properties-plugin
This is a plugin for Intellij IDEA which takes a series of property files (one per environment, plus an optional `application.properties`), and 
puts all the common and non-secret stuff into `application.properties` (if that doesn't exist, it's created), and then only environment-specific stuff
(dev is different from qa, for instance) and secret stuff (passwords, etc) are in the per-environment files.

The idea behind this is to help unclutter property files, which are often edited by hand, with lots of duplication and opportunities to
accidentally get one environment different from the others, when they're supposed to be the same.

So, anything that's common across all environments goes into `application.properties`

In the plugin settings, you can add to the list of keywords that mark keys as secret.  The obvious ones are stuff like "password", "secret", etc. 
You may have terms like "accesstoken", or prefer usernames also be considered secret.  If so, add them to this list.

What does this look like in practice?  Imagine you've got an app which has a database, and some AWS stuff.  Each environment has its own property file
(at work, we put ours into AWS Secrets Manager).

`dev.properties`
- hibernate.inClause.size                              = 1000
- hibernate.showSql                                    = false
- hikari.config.cachePreparedStatements                = true
- hikari.config.cachePreparedStatementsSize            = 250
- hikari.config.cachePreparedStatementsSqlLimit        = 2048
- hikari.config.cacheResultSetMetadata                 = true
- hikari.config.cacheServerConfiguration               = true
- hikari.config.connectionTimeout                      = 1000
- hikari.config.driverClassName                        = com.mysql.jdbc.Driver
- hikari.config.elideSetAutoCommits                    = true
- hikari.config.maintainTimeStats                      = true
- hikari.config.poolName                               = database
- hikari.config.rewriteBatchedStatements               = true
- hikari.config.useLocalSessionState                   = true
- hikari.config.useLocalTransactionState               = true
- hikari.config.useServerPreparedStatements            = true
- hikari.config.maxPoolSize                            = 10
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database-dev.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 1234567890
- amazon.key.access                                    = XXXXXXXXXXXX
- amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh

`qa.properties`
- hibernate.inClause.size                              = 1000
- hibernate.showSql                                    = false
- hikari.config.cachePreparedStatements                = true
- hikari.config.cachePreparedStatementsSize            = 250
- hikari.config.cachePreparedStatementsSqlLimit        = 2048
- hikari.config.cacheResultSetMetadata                 = true
- hikari.config.cacheServerConfiguration               = true
- hikari.config.connectionTimeout                      = 1000
- hikari.config.driverClassName                        = com.mysql.jdbc.Driver
- hikari.config.elideSetAutoCommits                    = true
- hikari.config.maintainTimeStats                      = true
- hikari.config.poolName                               = database
- hikari.config.rewriteBatchedStatements               = true
- hikari.config.useLocalSessionState                   = true
- hikari.config.useLocalTransactionState               = true
- hikari.config.useServerPreparedStatements            = true
- hikari.config.maxPoolSize                            = 10
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database-qa.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 1234567890
- amazon.key.access                                    = XXXXXXXXXXXX
- amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh

`prod.properties`
- hibernate.inClause.size                              = 1000
- hibernate.showSql                                    = false
- hikari.config.cachePreparedStatements                = true
- hikari.config.cachePreparedStatementsSize            = 250
- hikari.config.cachePreparedStatementsSqlLimit        = 2048
- hikari.config.cacheResultSetMetadata                 = true
- hikari.config.cacheServerConfiguration               = true
- hikari.config.connectionTimeout                      = 1000
- hikari.config.driverClassName                        = com.mysql.jdbc.Driver
- hikari.config.elideSetAutoCommits                    = true
- hikari.config.maintainTimeStats                      = true
- hikari.config.poolName                               = database
- hikari.config.rewriteBatchedStatements               = true
- hikari.config.useLocalSessionState                   = true
- hikari.config.useLocalTransactionState               = true
- hikari.config.useServerPreparedStatements            = true
- hikari.config.maxPoolSize                            = 10
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 0987654321
- amazon.key.access                                    = YYYYYYYYYY
- amazon.key.secret                                    = oiuyouwoiuywouywoiuyoiweuyowiuy

There's a LOT of duplicates up there - really, almost all of it's the same thing - but what's the easiest way to clean it up?

Select the property files (and an existing application.properties, if you have one - it'll create one if you don't), and then select `Align Property Files` from the `File` menu - you'll
see each file as a counterpart with a "_new" in the name (rather than overwrite the old values).  In our example above, we'd get this:

`application_new.properties` (notice - nothing sensitive or secret)
- hibernate.inClause.size                              = 1000
- hibernate.showSql                                    = false
- hikari.config.cachePreparedStatements                = true
- hikari.config.cachePreparedStatementsSize            = 250
- hikari.config.cachePreparedStatementsSqlLimit        = 2048
- hikari.config.cacheResultSetMetadata                 = true
- hikari.config.cacheServerConfiguration               = true
- hikari.config.connectionTimeout                      = 1000
- hikari.config.driverClassName                        = com.mysql.jdbc.Driver
- hikari.config.elideSetAutoCommits                    = true
- hikari.config.maintainTimeStats                      = true
- hikari.config.poolName                               = database
- hikari.config.rewriteBatchedStatements               = true
- hikari.config.useLocalSessionState                   = true
- hikari.config.useLocalTransactionState               = true
- hikari.config.useServerPreparedStatements            = true
- hikari.config.maxPoolSize                            = 10

`dev_new.properties`
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database-dev.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 1234567890
- amazon.key.access                                    = XXXXXXXXXXXX
- amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh

`qa_new.properties`
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database-qa.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 1234567890
- amazon.key.access                                    = XXXXXXXXXXXX
- amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh

`prod_new.properties`
- spring.datasource.dra.password                       = itsASecret
- spring.datasource.dra.url                            = jdbc:mysql://some-database.us-west-2.rds.amazonaws.com:3306/dibble
- amazon.account.id                                    = 0987654321
- amazon.key.access                                    = YYYYYYYYYY
- amazon.key.secret                                    = oiuyouwoiuywouywoiuyoiweuyowiuy

Now, if you need a new environment (say, UAT), you just need to create a file with 5 props instead of 20.  Simple, easy, and clear.

## How to run it
Select the property files you want to play with - then either
- Right click and select the `Align Property Files` action
- Or, Select `Align Propety Files` from the `File` menu
- Or, press `ctrl alt shift a` (or `cmd alt shift a` on a Mac)

## What's a Secret?
Any key with the word "secret" or "password" should be considered a secret.  But, what about keys which have  "access" and "key" together (as AWS keys are)?

In the settings page for the plugin, you can add extra keywords, one per line.  Adding `&&` will mark secret only those keys which have both tokens in it.  For example, if we
have a property file which contains the secret:
```
aws.access.key = XLKDJLSKDJLSKJS
```

We can add the line
```
access && key
```
and only those lines with both terms would be secrets.

## Todos
- It would be nice to add an option where if values are similar for _most_ environments, then that would go into application.properties, and the (few) 
environments that differed would have their own values
- Allow users to customize the "base" file name (currently hardcoded to "application.properties")
- Allow users to customize the names of the "new" files, including putting into a different folder