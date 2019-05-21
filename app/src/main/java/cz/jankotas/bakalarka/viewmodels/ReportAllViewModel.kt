package cz.jankotas.bakalarka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.services.reportFetcher.ReportDataSource
import cz.jankotas.bakalarka.services.reportFetcher.ReportDataSourceFactory

class ReportAllViewModel : ViewModel() {

    internal var itemPagedList: LiveData<PagedList<Report>>
    private var liveDataSource: LiveData<PageKeyedDataSource<Int, Report>>

    init {

        val itemDataSourceFactory = ReportDataSourceFactory()
        liveDataSource = itemDataSourceFactory.reportLiveDataSource

        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(ReportDataSource.PAGE_SIZE).build()

        itemPagedList = LivePagedListBuilder(itemDataSourceFactory, config).build()

    }
}