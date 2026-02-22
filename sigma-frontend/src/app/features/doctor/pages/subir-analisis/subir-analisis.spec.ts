import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubirAnalisis } from './subir-analisis';

describe('SubirAnalisis', () => {
  let component: SubirAnalisis;
  let fixture: ComponentFixture<SubirAnalisis>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubirAnalisis]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubirAnalisis);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
