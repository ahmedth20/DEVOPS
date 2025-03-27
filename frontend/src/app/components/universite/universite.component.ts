import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CoreService } from 'src/app/core/core.service';
import { DepartementService } from 'src/app/services/departement.service';
import { UniversiteService } from 'src/app/services/universite.service';

@Component({
  selector: 'app-universite',
  templateUrl: './universite.component.html',
  styleUrls: ['./universite.component.scss']
})
export class UniversiteComponent implements OnInit {
  universiteForm: FormGroup;
  departementsList: any[] = [];

  constructor(
    private _fb: FormBuilder,
    private _universiteService: UniversiteService,
    private _departementService: DepartementService,
    private _dialogRef: MatDialogRef<UniversiteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private _coreService: CoreService
  ) {
    this.universiteForm = this._fb.group({
      nomUniv: '',
      anneeCreation: '',
      budget: '',
      departements: []
    });
  }
  compareDepartement(option: any, selected: any): boolean {
    return option && selected ? option.idDepart === selected.idDepart : option === selected;
  }
  
  
  ngOnInit(): void {
    console.log( this._departementService.getDepartements());
    console.log( "this._departementService.getDepartements()");
    if (this.data) {
      this.universiteForm.patchValue({
        nomUniv: this.data.nomUniv,
        anneeCreation: this.data.anneeCreation,
        budget: this.data.budget,
        departements: this.data.departements || []
      });
    }
  
    this._departementService.getDepartements().subscribe({
      next: (departements) => {
        this.departementsList = departements;
        console.log(departements);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des départements :', err);
      }
    });
  }
  
  onFormSubmit() {
    if (this.universiteForm.valid) {
      const universiteData = {
        ...this.universiteForm.value,
        departements: this.universiteForm.value.departements.map((d: any) => ({ idDepart: d.idDepart, nomDepart: d.nomDepart }))
      };
  
      if (this.data) {
        universiteData.idUniv = this.data.idUniv;
        this._universiteService.updateUniversite(universiteData).subscribe({
          next: () => {
            this._coreService.openSnackBar('Université mise à jour avec succès!');
            this._dialogRef.close(true);
          },
          error: (err) => console.error(err),
        });
      } else {
        this._universiteService.addUniversite(universiteData).subscribe({
          next: () => {
            this._coreService.openSnackBar('Université ajoutée avec succès!');
            this._dialogRef.close(true);
          },
          error: (err) => console.error(err),
        });
      }
    }
  }
  
}
