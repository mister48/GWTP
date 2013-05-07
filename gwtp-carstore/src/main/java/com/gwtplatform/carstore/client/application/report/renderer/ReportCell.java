package com.gwtplatform.carstore.client.application.report.renderer;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.inject.Inject;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;

public class ReportCell extends AbstractCell<ManufacturerRatingDto> {
    interface Renderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name, String average);
    }

    private final Renderer uiRenderer;
    private final NumberFormat numberFormat;

    @Inject
    ReportCell(Renderer uiRenderer) {
        this.uiRenderer = uiRenderer;
        this.numberFormat = NumberFormat.getFormat("#,##0.0#");
    }

    @Override
    public void render(Context context, ManufacturerRatingDto value, SafeHtmlBuilder safeHtmlBuilder) {
        uiRenderer.render(safeHtmlBuilder, value.getManufacturer(), numberFormat.format(value.getRating()));
    }
}
