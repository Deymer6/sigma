import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisResultados } from './mis-resultados';

describe('MisResultados', () => {
  let component: MisResultados;
  let fixture: ComponentFixture<MisResultados>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisResultados]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MisResultados);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
