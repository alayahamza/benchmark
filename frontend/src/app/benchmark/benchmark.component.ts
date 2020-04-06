import {Component, OnInit} from '@angular/core';
import {BenchmarkService} from "../core/benchmark.service";
import {FinalResult} from "../core/model/final-result";

@Component({
  selector: 'app-benchmark',
  templateUrl: './benchmark.component.html',
  styleUrls: ['./benchmark.component.scss']
})
export class BenchmarkComponent implements OnInit {

  displayUploadResultButton = true;
  chartData: any;
  finalResult : FinalResult;

  constructor(private _benchmarkService: BenchmarkService) {
  }

  ngOnInit(): void {
  }

  uploadFile(event) {
    this._benchmarkService.uploadFile(event.target.files.item(0)).subscribe((data) => {
      this.finalResult = data;
      this.chartData = data.sectionStatistics;
      this.displayUploadResultButton = false;
    },error => console.error(error));
  }

  reset() {
    this.displayUploadResultButton = true;
  }
}
