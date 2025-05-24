package co.touchlab.kmmbridgekickstart.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public class TableQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    name: String,
    favorite: Boolean,
  ) -> T): Query<T> = Query(-843_706_627, arrayOf("Breed"), driver, "Table.sq", "selectAll",
      "SELECT Breed.id, Breed.name, Breed.favorite FROM Breed") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getBoolean(2)!!
    )
  }

  public fun selectAll(): Query<Breed> = selectAll { id, name, favorite ->
    Breed(
      id,
      name,
      favorite
    )
  }

  public fun <T : Any> selectById(id: Long, mapper: (
    id: Long,
    name: String,
    favorite: Boolean,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getBoolean(2)!!
    )
  }

  public fun selectById(id: Long): Query<Breed> = selectById(id) { id_, name, favorite ->
    Breed(
      id_,
      name,
      favorite
    )
  }

  public fun <T : Any> selectByName(name: String, mapper: (
    id: Long,
    name: String,
    favorite: Boolean,
  ) -> T): Query<T> = SelectByNameQuery(name) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getBoolean(2)!!
    )
  }

  public fun selectByName(name: String): Query<Breed> = selectByName(name) { id, name_, favorite ->
    Breed(
      id,
      name_,
      favorite
    )
  }

  public fun insertBreed(name: String) {
    driver.execute(629_246_003, """
        |INSERT OR IGNORE INTO Breed(name)
        |VALUES (?)
        """.trimMargin(), 1) {
          bindString(0, name)
        }
    notifyQueries(629_246_003) { emit ->
      emit("Breed")
    }
  }

  public fun deleteAll() {
    driver.execute(513_559_534, """DELETE FROM Breed""", 0)
    notifyQueries(513_559_534) { emit ->
      emit("Breed")
    }
  }

  public fun updateFavorite(favorite: Boolean, id: Long) {
    driver.execute(-1_147_464_563, """UPDATE Breed SET favorite = ? WHERE id = ?""", 2) {
          bindBoolean(0, favorite)
          bindLong(1, id)
        }
    notifyQueries(-1_147_464_563) { emit ->
      emit("Breed")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Breed", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Breed", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-385_060_362,
        """SELECT Breed.id, Breed.name, Breed.favorite FROM Breed WHERE id = ?""", mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "Table.sq:selectById"
  }

  private inner class SelectByNameQuery<out T : Any>(
    public val name: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Breed", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Breed", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-675_670_874,
        """SELECT Breed.id, Breed.name, Breed.favorite FROM Breed WHERE name = ?""", mapper, 1) {
      bindString(0, name)
    }

    override fun toString(): String = "Table.sq:selectByName"
  }
}
