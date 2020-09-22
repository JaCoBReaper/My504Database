import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.learnenglish504.R


// Custom pager adapter not using fragments
internal class CustomPagerAdapter(

    val context: Context,
    private val engList: ArrayList<String>,
    private val perList: ArrayList<String>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int = engList.size

    override fun instantiateItem(container: ViewGroup, position: Int): View {

        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.word_details_bottom, container, false)

        val txtWordPer: TextView = itemView.findViewById(R.id.viewPager_txv_per)
        val txtWordEng: TextView = itemView.findViewById(R.id.viewPager_txv_eng)

        txtWordPer.text = engList[position]
        txtWordEng.text = perList[position]

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }


}