package com.hardy.fawatir.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateAccountSettingsForm {

    @NotNull(message = "Enabled cannot be null or empty !")
    private Boolean enabled;
    @NotNull(message = "Not Locked cannot be null or empty !")
    private Boolean notLocked;

}
