package kr.goldenmine

import javafx.util.Pair
import kr.goldenmine.models.Figure
import kr.goldenmine.models.PhysicalObject
import kr.goldenmine.models.SkyRectangle
import java.util.*

class PointStorage {
    // 점 또는 면들
    val figures = ArrayList<Pair<Figure, Boolean>>()
//    val objects = LinkedList<PhysicalObject>()

    val lastCollided = HashMap<SkyRectangle, SkyRectangle>()

    fun addFigure(figure: Figure, editable: Boolean) {
        figures.add(Pair(figure, editable))
    }

    fun addFigure(figure: Figure) {
        addFigure(figure, false)
    }

//    fun addObject(obj: PhysicalObject) {
//        objects.add(obj)
//    }
}