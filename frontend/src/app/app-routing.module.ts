import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BenchmarkComponent} from "./benchmark/benchmark.component";


const routes: Routes = [
  {
    path: '', redirectTo: '/home',
    pathMatch: 'full'
  },

  {path: 'home', component: BenchmarkComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
