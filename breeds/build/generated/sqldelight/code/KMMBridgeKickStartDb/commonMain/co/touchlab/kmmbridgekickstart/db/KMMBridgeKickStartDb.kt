package co.touchlab.kmmbridgekickstart.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import co.touchlab.kmmbridgekickstart.db.breeds.newInstance
import co.touchlab.kmmbridgekickstart.db.breeds.schema
import kotlin.Unit

public interface KMMBridgeKickStartDb : Transacter {
  public val tableQueries: TableQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = KMMBridgeKickStartDb::class.schema

    public operator fun invoke(driver: SqlDriver): KMMBridgeKickStartDb =
        KMMBridgeKickStartDb::class.newInstance(driver)
  }
}
