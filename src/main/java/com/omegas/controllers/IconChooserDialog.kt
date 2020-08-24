package com.omegas.controllers

import com.omegas.main.Main
import com.omegas.model.Icon
import com.omegas.services.ImageSaveService.saveTemplatePng
import com.omegas.util.AlertType
import com.omegas.util.CreateType
import com.omegas.util.functions.applyIcon
import com.omegas.util.functions.getImage
import com.omegas.util.functions.showMessage
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class IconChooserDialog(var iconImages:List<BufferedImage>, private val createType: CreateType, private val file: File) : Initializable {
    @FXML
    lateinit var  iconOne: ImageView
    @FXML
    lateinit var  iconTwo:ImageView
    @FXML
    lateinit var  iconThree:ImageView

    @FXML
    lateinit var  chkBoxOne: CheckBox
    @FXML
    lateinit var  chkBoxTwo:CheckBox
    @FXML
    lateinit var  chkBoxThree:CheckBox

    @FXML
    lateinit var btnSelect:Button

    private var stage: Stage
    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/IconChooserDialog.fxml"))
        fxmlLoader.setController(this)
        val root :Parent = fxmlLoader.load()

        val scene = Scene(root)
        stage = Stage()
        stage.title = Main.TITLE+" - Icon Selection"
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.icons.add(Image(javaClass.getResource("/icon.png").toString()))
        stage.scene = scene
        stage.isResizable = false
    }
    fun show(){
        stage.show()
    }
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnSelect.setOnAction {
            select()
        }
        iconOne.image = getImage(iconImages[0])
        iconTwo.image = getImage(iconImages[1])
        iconThree.image = getImage(iconImages[2])

        chkBoxTwo.isSelected = true

        chkBoxOne.setOnAction {
            if(chkBoxOne.isSelected){
                chkBoxTwo.isSelected = false
                chkBoxThree.isSelected = false
            }
        }

        chkBoxTwo.setOnAction {
            if(chkBoxTwo.isSelected){
                chkBoxOne.isSelected = false
                chkBoxThree.isSelected = false
            }
        }

        chkBoxThree.setOnAction {
            if(chkBoxThree.isSelected){
                chkBoxTwo.isSelected = false
                chkBoxOne.isSelected = false
            }
        }

    }

    fun select(){
        val index= when{
            chkBoxOne.isSelected ->{0}
            chkBoxTwo.isSelected->{1}
            chkBoxThree.isSelected->{2}
            else -> {-1}
        }
        if(index>=0){
            val image = iconImages[index]
            when(createType){
                CreateType.CREATE -> {
                    thread(true) {
                        createIcon(image,true)
                        showMessage("Icon created successfully", AlertType.INFO, "Icon saved to folder ${file.name}")
                    }
                }
                CreateType.CREATE_AND_APPLY -> createAndApply(image)
            }
            btnSelect.scene.window.hide()
        }
    }
    private fun createIcon(bufferedImage: BufferedImage, delete:Boolean):Icon?{
        val pngFile = saveTemplatePng(bufferedImage, file)
        return com.omegas.util.functions.createIcon(pngFile, delete)
    }
    private fun createAndApply(bufferedImage: BufferedImage){
        thread(true) {
            val icon = createIcon(bufferedImage,false)
            applyIcon(icon,this.file)
        }
    }
}