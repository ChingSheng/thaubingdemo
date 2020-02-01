package scottychang.thaubing.model

import com.google.gson.annotations.SerializedName


class CorpPenaltyRecord {
    @SerializedName("corp_name")
    val corpName: String? = null

    @SerializedName("corp_id")
    val corpId: String? = null

    @SerializedName("penalty_count")
    val penaltyCount: Int? = null

    @SerializedName("penalty_sum")
    val penaltySum: Long? = null

    @SerializedName("penalty_detail")
    val penaltyDetails: List<PenaltyDetail?>? = null

    class PenaltyDetail {
        @SerializedName("統一編號")
        val taxID: String? = null

        @SerializedName("企業名稱")
        val corpName: String? = null

        @SerializedName("列管事業編號")
        val businessID: String? = null

        @SerializedName("事業名稱")
        val businessName: String? = null

        @SerializedName("產業類型")
        val businessType: String? = null

        @SerializedName("裁罰日期")
        val penaltyDate: String? = null

        @SerializedName("違規日期")
        val violationDate: String? = null

        @SerializedName("縣市")
        val city: String? = null

        @SerializedName("主題")
        val topic: String? = null

        @SerializedName("文號")
        val topicID: String? = null

        @SerializedName("移送法規")
        val regulation: String? = null

        @SerializedName("裁處事由")
        val sanction: String? = null

        @SerializedName("是否提出訴願")
        val appeal: String? = null

        @SerializedName("訴願結果")
        val appealResult: String? = null

        @SerializedName("限改日期")
        val improvementDeadLine: String? = null

        @SerializedName("改善完妥")
        val improvementStatus: String? = null

        @SerializedName("強制移送")
        val forcedTransfer: String? = null

        @SerializedName("費用繳清狀態")
        val penaltyPayStatus: String? = null

        @SerializedName("裁罰費用")
        val penalty: Int? = null

        @SerializedName("是否確認裁罰")
        val penaltyCheckedStatus: String? = null
    }
}