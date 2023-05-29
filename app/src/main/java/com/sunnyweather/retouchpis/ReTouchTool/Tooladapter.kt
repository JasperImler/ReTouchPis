package com.sunnyweather.retouchpis.ReTouchTool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.retouchpis.R
import com.sunnyweather.retouchpis.ToolLogic

class Tooladapter(val toolList:List<Tool>,val retouchingphoto:ImageView):RecyclerView.Adapter<Tooladapter.ViewHolder>() {
    private var lastClickPosition:Int = 0
    lateinit var re:RecyclerView
    var lastProgress:Float = 0F //获取上一次的进度

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view)
    {
       val ToolImage = view.findViewById<ImageView>(R.id.toolImage)
        val ToolName = view.findViewById<TextView>(R.id.toolname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tools_item,parent,false)
        re = parent.findViewById(R.id.toolList)

        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val curClickPosition = holder.bindingAdapterPosition//获取当前位置
           if(lastClickPosition!=curClickPosition) //当前位置与上一次点击位置不一致
            {
                val toolLogic = ToolLogic(holder,retouchingphoto,0F,view)//获取实例
                toolLogic.ChangeState()//首先更改当前点击颜色


                val lastHolder = re.findViewHolderForAdapterPosition(lastClickPosition) as Tooladapter.ViewHolder
                val lastToolLogic = ToolLogic(lastHolder,retouchingphoto,lastProgress,view)//获取上一个的实例
                lastToolLogic.ChangeBack()//改变回原来的颜色

                //更新

                lastProgress = toolLogic.getLastPregress()//更新上一次进度
                lastClickPosition = holder.bindingAdapterPosition

                toolLogic.show()//开启当前功能实现
            }else //与上一次点击位置一致
            {
                val toolLogic = ToolLogic(holder,retouchingphoto,lastProgress,view)//获取实例
                toolLogic.ChangeState()

                lastClickPosition = holder.bindingAdapterPosition//更新点击位置
                lastProgress=toolLogic.getLastPregress() //更新上一次点击进度

                toolLogic.show()//开启当前功能实现
            }
        }
        return holder
    }

    override fun getItemCount()=toolList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tools = toolList[position]
        holder.ToolImage.setImageResource(tools.Toolid)
        holder.ToolName.setText(tools.Toolname)
    }
}