import {autoinject, PLATFORM} from "aurelia-framework";
import {NavModel, Router, RouterConfiguration} from 'aurelia-router';
import {DataLoader} from "./services/data-loader";
import {StatusConverter} from "./services/status-converter";
import {data} from "./services/report-model";
import {MdcDrawer} from "@aurelia-mdc-web/drawer";
import "./app.scss"
import {StatisticsGenerator} from "./services/statistics-generator";
import IExecutionContext = data.IExecutionContext;

@autoinject()
export class App {
    private _router: Router;
    private _drawer:MdcDrawer;
    private _executionContext: IExecutionContext;
    private _routeConfig: RouterConfiguration;

    constructor(
        private _dataLoader: DataLoader,
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
    ) {
    }

    attached() {
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._executionContext = executionStatistics.executionAggregate.executionContext;
            this._routeConfig.title = this._executionContext.runConfig.reportName;
            this._router.routes.filter(route => route.route == "failure-aspects").find(route => {
                route.settings.count = executionStatistics.failureAspectStatistics.length;
            });
            this._router.routes.filter(route => route.route == "exit-points").find(route => {
                route.settings.count = executionStatistics.exitPointStatistics.length;
            });
        })
    }

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        this._routeConfig = config;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('components/dashboard/dashboard'),
                nav: true,
                name: "dashboard",
                title: 'Dashboard'
            },
            {
                route: 'classes',
                moduleId: PLATFORM.moduleName('components/classes/classes'),
                nav: true,
                name: "classes",
                title: 'Classes'
            },
            // {
            //     route: 'threads',
            //     moduleId: PLATFORM.moduleName('components/threads'),
            //     nav: true,
            //     title: 'Threads'
            // },
            {
                route: 'failure-aspects',
                moduleId: PLATFORM.moduleName('components/failure-aspects/failure-aspects'),
                nav: true,
                title: 'Failure Aspects',
                name: "failure-aspects",
                settings: {
                    count: 0
                }
            },
            {
                route: 'method',
                moduleId: PLATFORM.moduleName('components/method-details/method-details'),
                nav: false,
                title: 'Method',
                name: "method",
            },
            // {
            //     route: 'exit-points',
            //     moduleId: PLATFORM.moduleName('components/exit-points/exit-points'),
            //     nav: true,
            //     name: "exit-points",
            //     title: 'Exit Points',
            //     settings: {
            //         count: 0
            //     }
            // },
            // {
            //     route: 'logs',
            //     name: 'Logs',
            //     moduleId: PLATFORM.moduleName('components/logs'),
            //     nav: true,
            //     title: 'Logs'
            // },
            // {
            //     route: 'timings',
            //     name: 'Timings',
            //     moduleId: PLATFORM.moduleName('components/timings'),
            //     nav: true,
            //     title: 'Timings'
            // },
            // {
            //     route: 'jvm-monitor',
            //     name: 'JVM Monitor',
            //     moduleId: PLATFORM.moduleName('components/jvm'),
            //     nav: true,
            //     title: 'JVM Monitor'
            // },
        ]);
    }

    navigateTo(nav:NavModel|string) {
        if (nav instanceof NavModel) {
            this._router.navigate(nav.href);
        } else {
            this._router.navigateToRoute(nav);
        }
        this._drawer.open = false;
    }
}


