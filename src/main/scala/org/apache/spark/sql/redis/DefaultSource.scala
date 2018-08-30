package org.apache.spark.sql.redis

import org.apache.spark.sql.SaveMode.{Append, ErrorIfExists, Ignore, Overwrite}
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, RelationProvider}
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}

class DefaultSource extends RelationProvider
  //  with SchemaRelationProvider
  with CreatableRelationProvider {

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    new RedisSourceRelation(sqlContext, parameters, userSpecifiedSchema = None)
  }

  /**
    * Creates a new relation by saving the data to Redis
    */
  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    val relation = new RedisSourceRelation(sqlContext, parameters, userSpecifiedSchema = None)

    mode match {
      case Append => relation.insert(data, overwrite = false)
      case Overwrite => relation.insert(data, overwrite = true)
      case ErrorIfExists =>
        // TODO: check if exists
        relation.insert(data, overwrite = false)
      case Ignore =>
        // TODO:
        ???
    }

    relation
  }


  //  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {
  //    new RedisSourceRelation(sqlContext, parameters)
  //  }
}