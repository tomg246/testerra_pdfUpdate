import {autoinject} from "aurelia-framework";
import "./test-results-card.scss";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import ApexOptions = ApexCharts.ApexOptions;

@autoinject
export class TestResultsCard {
    private _apexPieOptions: ApexOptions = undefined;
    private _executionStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._preparePieChart(executionStatistics);
        });
    }

    private _preparePieChart(executionStatistics: ExecutionStatistics): void {
        const series = [];
        const labels = [];
        const colors = [];

        for (const status of this._statusConverter.relevantStatuses) {
            series.push(executionStatistics.getStatusCount(status));
            labels.push(this._statusConverter.getLabelForStatus(status));
            colors.push(this._statusConverter.getColorForStatus(status));
        }

        this._apexPieOptions = {
            chart: {
                type: 'pie',
                width: '400px',
                fontFamily: 'Roboto'
            },
            series: series,
            colors: colors,
            labels: labels
        };
    }
}
