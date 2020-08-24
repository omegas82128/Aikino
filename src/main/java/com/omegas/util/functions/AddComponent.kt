package com.omegas.util.functions

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane

fun addComponent(pane: Pane, component: Node){
    pane.children.clear()
    for(i in 1..7){
        pane.children.add(Label(""))
    }
    val border = BorderPane()
    border.center= component
    pane.children.add(border)
}