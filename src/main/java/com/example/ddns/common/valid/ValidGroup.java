package com.example.ddns.common.valid;


import jakarta.validation.groups.Default;

/**
 * @author sssd
 */
public class ValidGroup {
    public interface SaveGroup extends Default {}
    public interface UpdateGroup extends Default{}
    public interface CopyGroup extends Default{}
}
