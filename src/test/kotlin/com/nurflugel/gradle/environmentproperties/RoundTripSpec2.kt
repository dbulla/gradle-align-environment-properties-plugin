package com.nurflugel.gradle.environmentproperties

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File

/** General full tests */
class RoundTripSpec2 : StringSpec(
        {

            val action = AlignPropertiesAction()
            val parent = File("src/test/resources/examples/sample4")
            val files = listOf(
                    File(parent, "application.properties"),
                    File(parent, "dev.properties"),
                    File(parent, "qa.properties"),
                    File(parent, "prod.properties")
            )
            action.processPropertyFiles(files)

            "test base".config(enabled = ALL_TESTS_ENABLED) {
                validateNewFile(
                        parent, "application_new.properties", listOf(
                        "hibernate.inClause.size                              = 1000",
                        "hibernate.showSql                                    = false",
                        "hikari.config.cachePreparedStatements                = true",
                        "hikari.config.cachePreparedStatementsSize            = 250",
                        "hikari.config.cachePreparedStatementsSqlLimit        = 2048",
                        "hikari.config.cacheResultSetMetadata                 = true",
                        "hikari.config.cacheServerConfiguration               = true",
                        "hikari.config.connectionTimeout                      = 1000",
                        "hikari.config.driverClassName                        = com.mysql.jdbc.Driver",
                        "hikari.config.elideSetAutoCommits                    = true",
                        "hikari.config.maintainTimeStats                      = true",
                        "hikari.config.poolName                               = database",
                        "hikari.config.rewriteBatchedStatements               = true",
                        "hikari.config.useLocalSessionState                   = true",
                        "hikari.config.useLocalTransactionState               = true",
                        "hikari.config.useServerPreparedStatements            = true",
                        "hikari.config.maxPoolSize                            = 10",
                        "http.connection.request.timeout                      = 1000",
                        "http.connection.timeout                              = 1000"
                )
                )
            }

            "test dev".config(enabled = ALL_TESTS_ENABLED) {
                validateNewFile(
                        parent, "dev_new.properties", listOf(
                        "spring.datasource.dra.password                       = itsASecret",
                        "spring.datasource.dra.url                            = jdbc:mysql://some-database-dev.us-west-2.rds.amazonaws.com:3306/dibble",
                        "amazon.account.id                                    = 1234567890",
                        "amazon.key.access                                    = XXXXXXXXXXXX",
                        "amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh"
                )
                )
            }

            "test qa".config(enabled = ALL_TESTS_ENABLED) {
                validateNewFile(
                        parent, "qa_new.properties", listOf(
                        "spring.datasource.dra.password                       = itsASecret",
                        "spring.datasource.dra.url                            = jdbc:mysql://some-database-qa.us-west-2.rds.amazonaws.com:3306/dibble",
                        "amazon.account.id                                    = 1234567890",
                        "amazon.key.access                                    = XXXXXXXXXXXX",
                        "amazon.key.secret                                    = aakjdhflakjdflakjfhladjfhalkdjhfaljfhaljdfh"
                )
                )
            }
            "test prod".config(enabled = ALL_TESTS_ENABLED) {
                validateNewFile(
                        parent, "prod_new.properties", listOf(
                        "spring.datasource.dra.password                       = itsASecret",
                        "spring.datasource.dra.url                            = jdbc:mysql://some-database.us-west-2.rds.amazonaws.com:3306/dibble",
                        "amazon.account.id                                    = 0987654321",
                        "amazon.key.access                                    = YYYYYYYYYY",
                        "amazon.key.secret                                    = oiuyouwoiuywouywoiuyoiweuyowiuy"
                )
                )
            }

        }) {
    companion object {
        // setting this to false lets you test a single method if you then set it's enabled flag to true.
        // Irritating that the framework does not value from dev"ns't do this better...

        // const val ALL_TESTS_ENABLED = false
        const val ALL_TESTS_ENABLED = true

        fun validateNewFile(parent: File, child: String, expectedLines: List<String>) {
            val newFile = File(parent, child)

            val lines = newFile.readLines()// ensure they're in the same order
            val sortedExpected = expectedLines.sorted().map {
                adjustLineForConsistency(
                        it
                )
            }
            val sortedActual = lines.sorted().map {
                adjustLineForConsistency(
                        it
                )
            }

            sortedActual shouldBe sortedExpected
            lines.size shouldBe expectedLines.size
        }

        /** Take the line and remove whitespace for testing */
        private fun adjustLineForConsistency(line: String): String {
            return line.substringBefore("=").trim() + " = " + line.substringAfter("=").trim()
        }

    }
}


