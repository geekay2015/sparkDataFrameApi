package org.myorg.spark

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by gangadharkadam on 5/6/16.
 */


object App {
  def main (args: Array[String]): Unit ={

    val conf = new SparkConf().setAppName("Test App").setMaster("local")
    val sc = new SparkContext(conf)

    val col = sc.parallelize(0 to 100 by 5)
    val smp = col.sample(true,4)

    val colCount = col.count()
    val smpCount = smp.count()

    println("orginal Count = " + colCount)
    println("Sample Count = " + smpCount)

    sc.stop()
  }

}
