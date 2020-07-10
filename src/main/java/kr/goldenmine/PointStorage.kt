package kr.goldenmine

import javafx.util.Pair
import kr.goldenmine.models.Figure
import java.util.*

class PointStorage {
    // 점 또는 면들
    val figures = ArrayList<Pair<Figure, Boolean>>()

    fun add(figure: Figure, editable: Boolean) {
        figures.add(Pair(figure, editable))
    }
}