package com.sisindia.ai.mtrainer.android.features.myunits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.sisindia.ai.mtrainer.android.models.UnitsData

class SearchAdapter (context: Context, @LayoutRes private val layoutResource: Int, private val allPois: List<UnitsData>):
        ArrayAdapter<UnitsData>(context, layoutResource, allPois),
        Filterable {
    private var mPois: List<UnitsData> = allPois

    override fun getCount(): Int {
        return mPois.size
    }

    override fun getItem(p0: Int): UnitsData? {
        return mPois.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        // Or just return p0
        return mPois.get(p0).unitId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = "${mPois[position].unitCode} ${mPois[position].unitName}"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                mPois = filterResults.values as List<UnitsData>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    allPois
                else
                    allPois.filter {
                        it.unitName.toLowerCase().contains(queryString) ||
                                it.unitCode.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}