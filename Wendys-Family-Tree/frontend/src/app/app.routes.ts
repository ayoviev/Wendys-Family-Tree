import {Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {HorseDetailComponent} from './component/horse/horse-detail/horse-detail.component';
import { OwnerListComponent } from './component/owner/owner-list/owner-list.component';
import { OwnerCreateComponent } from './component/owner/owner-create/owner-create.component';
import { HorseFamilyTreeComponent } from './component/horse/horse-family-tree/horse-family-tree.component';

export const routes: Routes = [
  {path: 'horses', children: [
    {path: '', component: HorseComponent, pathMatch: 'full'},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
    {path: ':id/edit', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
    {path: ':id/familytree', component: HorseFamilyTreeComponent},
    {path: ':id', component: HorseDetailComponent}
  ]},
  {path: 'owners', children: [
    {path: '', component: OwnerListComponent, pathMatch: 'full'},
    {path: 'create', component: OwnerCreateComponent},
  ]},
  {path: '**', redirectTo: 'horses'},
];
