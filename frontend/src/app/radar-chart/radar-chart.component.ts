import {Component, Input, OnInit} from '@angular/core';

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
    this.radarChartData = [
      {data: this.arr, label: 'SG'}
      // ,
      // {data: [90, 150, 200, 45, 210], label: 'CACIB'},
      // {data: [100, 160, 220, 55, 220], label: 'BNP'}
    ];
  }
}
