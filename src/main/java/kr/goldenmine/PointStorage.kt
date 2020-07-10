package kr.goldenmine

import javafx.util.Pair
import kr.goldenmine.models.Figure
import java.util.*

class PointStorage {
    // 점 또는 면들
    val figures: List<Pair<Figure, Boolean>> = ArrayList()
}