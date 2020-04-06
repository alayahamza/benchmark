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
  sectionChartData = [];
  subSectionChartData = [];
  sectionChartLabels = [];
  subSectionChartLabels = [];
  finalResult: FinalResult;

  constructor(@Inject(APP_BASE_HREF) baseHref: string, private _benchmarkService: BenchmarkService) {
    this.baseHref = baseHref;
  }

  ngOnInit(): void {
  }

  uploadFile(event) {
    this._benchmarkService.uploadFile(event.target.files.item(0)).subscribe((data) => {
      this.finalResult = data;
      this.extractSectionData();
      this.extractSubSectionData();
      this.displayUploadResultButton = false;
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
  }
}
