package scottychang.thaubing.util

class ScannedResultUtil {
        companion object {
            fun isTaxId(rawString: String): Boolean {
                return rawString.length == 8
                        && isDigitString(rawString)
            }

            fun isGS1Bar(rawString: String): Boolean {
                return rawString.length == 13
                        && rawString.substring(0,3).equals("471")   // Region code: TW
                        && isDigitString(rawString.substring(3, 13))
            }

            fun isReceiptBar(rawString: String): Boolean {
                return rawString.length == 19
                        && isDigitString(rawString.substring(0, 5))
                        && isCharacterString(rawString.substring(5, 7))
                        && isDigitString(rawString.substring(7, 19))
            }

            fun isReceiptQR(rawString: String): Boolean {
                return rawString.length >= 77
                        && isCharacterString(rawString.substring(0,2))
                        && isDigitString(rawString.substring(2, 21))
                        && isHex(rawString.substring(21, 37))
                        && isDigitString(rawString.substring(37, 53))
                        && isBase64(rawString.substring(53, 77))
            }

            private fun isHex(string: String): Boolean {
                return string.all { char -> char.isDigit()
                        || char.equals('a', true)
                        || char.equals('b', true)
                        || char.equals('c', true)
                        || char.equals('d', true)
                        || char.equals('e', true)
                        || char.equals('f', true)}
            }

            fun isDigitString(string: String): Boolean {
                return string.all { char -> char.isDigit() }
            }

            fun isCharacterString(string: String): Boolean {
                return string.all { char -> char.isLetter() }
            }

            fun isBase64(string: String): Boolean {
                return string.all {char -> char.isLetterOrDigit() || char.equals('=') || char.equals('+') || char.equals('/') }
            }
        }
}