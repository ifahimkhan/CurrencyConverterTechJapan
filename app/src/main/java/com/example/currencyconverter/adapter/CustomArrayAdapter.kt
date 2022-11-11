package com.example.currencyconverter.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.currencyconverter.R
import com.example.currencyconverter.model.CountryModel
import java.util.*

class CustomArrayAdapter(
    context: Context,
    item_spinner: Int,
    stafuserList: ArrayList<CountryModel>
) : ArrayAdapter<CountryModel>(
    context, item_spinner, stafuserList
), Filterable {
    var users = ArrayList<CountryModel>()
    var fixedusers = ArrayList<CountryModel>()
    var myFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterString = constraint.toString().lowercase(Locale.getDefault())
            val results = FilterResults()
            val list = fixedusers
            val count = list.size
            val nlist = ArrayList<CountryModel>(count)
            var filterableString: CountryModel
            for (i in 0 until count) {
                filterableString = list[i]
                if (filterableString.currencyy.lowercase().contains(filterString) ||
                    filterableString.name.lowercase().contains(filterString)
                ) {
                    nlist.add(filterableString)
                }
            }
            results.values = nlist
            results.count = nlist.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            Log.e("", constraint.toString().isEmpty().toString() + "" + results.count)
            if (constraint.toString().isEmpty() || results.count == 0) {
                users.clear()
                users.addAll(fixedusers)
                notifyDataSetChanged()
            } else {
                users.clear()
                users.addAll((results.values as Collection<CountryModel>))
                notifyDataSetChanged()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

//        String innserUser = getItem(position);
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.custom_layout, parent, false)
        }
        val username = convertView!!.findViewById<TextView>(R.id.tv_username)
        val name = convertView.findViewById<TextView>(R.id.tv_name)
        try {
            username.text = users[position].currencyy
            name.text = users[position].name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    fun setUsersData() {
        users.clear()
        users.addAll(fixedusers)
    }

    init {
        fixedusers.clear()
        fixedusers.addAll(stafuserList)
        users = stafuserList
    }
}