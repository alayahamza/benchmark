import {Component, Inject, OnInit} from '@angular/core';
import {BenchmarkService} from "../core/benchmark.service";
import {FinalResult} from "../core/model/final-result";
import {APP_BASE_HREF} from "@angular/common";

@Component({
  selector: 'app-benchmark',
  templateUrl: './benchmark.component.html',
  styleUrls: ['./benchmark.component.scss']
})
export class BenchmarkComponent implements OnInit {

  baseHref;

  displayUploadResultButton = true;
  displayCharts = false;
  sectionChartData = [];
  subSectionChartData = [];
  sectionChartLabels = [];
  subSectionChartLabels = [];
  finalResult: FinalResult;
  displayProgressBar = false;

  constructor(@Inject(APP_BASE_HREF) baseHref: string, private _benchmarkService: BenchmarkService) {
    this.baseHref = baseHref;
  }

  ngOnInit(): void {
  }

  uploadFile(event) {
    this.displayProgressBar = true;
    this.displayUploadResultButton = false;
    this._benchmarkService.uploadFile(event.target.files.item(0)).subscribe((data) => {
      this.finalResult = data;
      this.extractSectionData();
      this.extractSubSectionData();
      this.displayProgressBar = false;
      this.displayCharts = true;
    }, error => console.error(error));
  }

  private extractSectionData() {
    this.sectionChartData = [];
    this.finalResult.sectionStatistics.forEach(company => {
      let averages = [];
      company.sections.forEach(section => {
        averages.push(section.average);
      });
      this.sectionChartData.push({data: averages, label: company.name});
    });
    this.finalResult.sectionStatistics[0].sections.forEach(section => this.sectionChartLabels.push(section.name));
  }

  private extractSubSectionData() {
    this.subSectionChartData = [];
    this.finalResult.subSectionStatistics.forEach(company => {
      let averages = [];
      company.sections.forEach(section => {
        averages.push(section.average);
      });
      this.subSectionChartData.push({data: averages, label: company.name});
    });
    this.finalResult.subSectionStatistics[0].sections.forEach(section => this.subSectionChartLabels.push(section.name));
  }

  reset() {
    this.displayUploadResultButton = true;
    this.displayProgressBar = false;
    this.displayCharts = false;
    this.sectionChartData = [];
    this.subSectionChartData = [];
    this.sectionChartLabels = [];
    this.subSectionChartLabels = [];
  }
}
