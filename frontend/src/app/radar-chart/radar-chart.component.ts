import {Component, Input, OnInit} from '@angular/core';
import {from, of, zip} from "rxjs";
import {groupBy, mergeMap, toArray} from "rxjs/operators";

@Component({
  selector: 'app-radar-chart',
  templateUrl: './radar-chart.component.html',
  styleUrls: ['./radar-chart.component.scss']
})
export class RadarChartComponent implements OnInit {
  @Input() data;
  public radarChartLabels;
  public radarChartData;
  public radarChartType = 'radar';
  arr = [];

  constructor() {
  }

  ngOnInit(): void {
    this.initChart();
  }

  initChart() {
    this.radarChartLabels = Object.keys(this.data);
    Object.keys(this.data).forEach((key) => {
      this.arr.push(this.data[key].average);
    })
    // let observable = from(this.arr).pipe(
    //   groupBy(element => element.company),
    //   mergeMap(group => group.pipe(toArray()))
    // );
    // observable.subscribe(val => console.log(val));
    // console.log(this.arr)
    // from(this.arr)
    //   .pipe(
    //     groupBy(
    //       element => element.company,
    //       e => e.name
    //     ),
    //     mergeMap(group => zip(of(group.key), group.pipe(toArray())))
    //   )
    //   .subscribe(console.log);
    this.radarChartData = [
      {data: this.arr, label: 'SG'}
      // ,
      // {data: [90, 150, 200, 45, 210], label: 'CACIB'},
      // {data: [100, 160, 220, 55, 220], label: 'BNP'}
    ];
  }
}
