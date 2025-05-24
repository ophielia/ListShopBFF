package co.touchlab.kmmbridgekickstart.db.breeds

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import co.touchlab.kmmbridgekickstart.db.KMMBridgeKickStartDb
import co.touchlab.kmmbridgekickstart.db.TableQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<KMMBridgeKickStartDb>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = KMMBridgeKickStartDbImpl.Schema

internal fun KClass<KMMBridgeKickStartDb>.newInstance(driver: SqlDriver): KMMBridgeKickStartDb =
    KMMBridgeKickStartDbImpl(driver)

private class KMMBridgeKickStartDbImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), KMMBridgeKickStartDb {
  override val tableQueries: TableQueries = TableQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE Breed (
          |id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |name TEXT NOT NULL UNIQUE,
          |favorite INTEGER NOT NULL DEFAULT 0
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
