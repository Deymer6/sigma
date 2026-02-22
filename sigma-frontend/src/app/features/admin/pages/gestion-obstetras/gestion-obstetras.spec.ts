import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionObstetras } from './gestion-obstetras';

describe('GestionObstetras', () => {
  let component: GestionObstetras;
  let fixture: ComponentFixture<GestionObstetras>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionObstetras]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionObstetras);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
