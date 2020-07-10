package com.testtube.gstreporter.views.vInterface

interface RecyclerViewInterface {

    fun onClick(pos: Int){}

    fun onClick(pos: Int, data: Any){}

    fun onAction(pos: Int, actionId: Actions, data: Any)

    public enum class Actions {
        Edit,
        Delete
    }

}