import {Component, Input, OnInit} from '@angular/core';
import {RadialChartOptions} from "chart.js";

@Component({
  selector: 'app-radar-chart',
  templateUrl: './radar-chart.component.html',
  styleUrls: ['./radar-chart.component.scss']
})
export class RadarChartComponent implements OnInit {
  @Input() public chartLabels;
  @Input() public chartData;
  public radarChartOptions: RadialChartOptions = {
    responsive: true,
  };
  public radarChartType = 'radar';

  constructor() {
  }

  ngOnInit(): void {
  }

}
