package ai.t2x.trace.common

object RasAuthdUtil {
  def get10StrToDate(date10Str: String) = {
    val format = new java.text.SimpleDateFormat("yyyyMMddHHmm")
    format.parse(date10Str)
  }
}
