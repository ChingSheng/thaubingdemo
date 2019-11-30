package scottychang.thaubing.model

import android.os.Parcel
import android.os.Parcelable
import scottychang.thaubing.util.ScannedResultUtil

class ScannedItem() : Parcelable {
    enum class Type {
        TAX_ID, RECEIPT_BAR, RECEIPT_QR, GS1_BAR, UNKNOWN
    }

    var rawString = ""
    var scannedType = Type.UNKNOWN
    var taxID = ""

    constructor(parcel: Parcel) : this() {
        rawString = parcel.readString() ?: ""
        taxID = parcel.readString() ?: ""
        scannedType = Type.values()[(parcel.readInt())]
    }

    constructor(rawString : String) : this() {
        this.rawString = rawString
        parse(rawString)
    }

    init {
        parse(rawString)
    }

    private fun parse(rawString: String) {
        when  {
            ScannedResultUtil.isTaxId(rawString) -> {
                scannedType = Type.TAX_ID
                taxID = rawString
            }
            ScannedResultUtil.isReceiptBar(rawString) -> {
                scannedType = Type.RECEIPT_BAR
                taxID = ""
            }
            ScannedResultUtil.isReceiptQR(rawString) -> {
                scannedType = Type.RECEIPT_QR
                taxID = rawString.substring(45, 53)
            }
            ScannedResultUtil.isGS1Bar(rawString) -> {
                scannedType = Type.GS1_BAR
                taxID = ""
            }
            else -> {
                scannedType = Type.UNKNOWN
                taxID = ""
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rawString)
        parcel.writeString(taxID)
        parcel.writeInt(scannedType.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScannedItem> {
        override fun createFromParcel(parcel: Parcel): ScannedItem {
            return ScannedItem(parcel)
        }

        override fun newArray(size: Int): Array<ScannedItem?> {
            return arrayOfNulls(size)
        }
    }
}