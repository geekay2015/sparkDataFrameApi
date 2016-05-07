package org.myorg.spark


import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}


case class Auction(
                  auctionid: String,
                  bid: Float,
                  bidtime: Float,
                  bidder: String,
                  bidderrate: Int,
                  openbid: Float,
                  price: Float,
                  item: String,
                  daystolive: Int
                    )

object ebayDataFrame {

  def main (args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ebayDataFrame").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlcontext = new SQLContext(sc)

    import sqlcontext.implicits._

    // load the data into RDD
    val ebayData = sc.textFile("/Users/gangadharkadam/myapps/myspark/sparkDataFrame/src/main/resources/ebay.csv")

    // return the first element in the RDD
    println(ebayData.first())

    println("******Top 10 rows******")
    ebayData.take(10).foreach(println)

    println("******Total count******")
    println(ebayData.count())

    // create RDD of auction Object
    val ebay = ebayData.map(_.split(","))
      .map(p =>
        Auction(p(0), p(1).toFloat, p(2).toFloat, p(3), p(4).toInt, p(5).toFloat, p(6).toFloat, p(7), p(8).toInt)
      )
    // Create data frame
    val auction = ebay.toDF()
    auction.show()
    auction.printSchema()

    // How many auction were held
    val auctioncCount = auction.select("auctionid").count()
    println(auctioncCount)

    // How many bids were made per item?
    auction.groupBy("auctionid","item").count().show()

    auction.registerTempTable("auctionData")
    val bidsPerItem = sqlcontext.sql("select item,count(distinct auctionid) " +
      "from auctionData group by item").show()

    // Get the auctions with closing price > 100
    auction.filter("price > 0").show()
    sqlcontext.sql(
      """
        SELECT auctionid, max(price)
        FROM auctionData
        GROUP BY auctionid
      """.stripMargin).show()

  }

}
