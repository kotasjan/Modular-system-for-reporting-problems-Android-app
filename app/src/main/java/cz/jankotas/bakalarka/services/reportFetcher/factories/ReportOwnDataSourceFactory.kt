package cz.jankotas.bakalarka.services.reportFetcher.factories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.services.reportFetcher.ReportOwnDataSource

/**
 * Třída, která předává získaná data z DataSource
 */
class ReportOwnDataSourceFactory : DataSource.Factory<Int, Report>() {

    // v tomto objektu jsou uloženy aktuálně zobrazované podněty a je zde uloženo také číslo stránky v seznamu
    // seznam se totiž rozdělí do stránek po určitém počtu podnětů, které se načítají postupně (aby bylo zobrazování plynulejší)
    val reportLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Report>> = MutableLiveData()

    // vytvoření a předání instance DataSource
    override fun create(): DataSource<Int, Report> {
        val reportDataSource = ReportOwnDataSource()
        reportLiveDataSource.postValue(reportDataSource)
        return reportDataSource
    }
}