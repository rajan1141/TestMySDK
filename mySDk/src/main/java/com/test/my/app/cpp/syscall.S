#include "bionic_asm.h"

#if defined(__aarch64__)   // ARM64 architecture

.text
.globl my_openat
.type my_openat, %function
        my_openat:
.cfi_startproc
        mov x8, x0            // Move syscall number into x8
mov x0, x1            // Move first argument into x0
mov x1, x2            // Move second argument into x1
mov x2, x3            // Move third argument into x2
mov x3, x4            // Move fourth argument into x3
mov x4, x5            // Move fifth argument into x4
mov x5, x6            // Move sixth argument into x5
svc #0                // Supervisor Call
cmp x0, #0            // Compare return value with 0 (success)
bge my_openat_done    // If >= 0, syscall succeeded
neg x0, x0            // Negate x0 to get error code
//b __set_errno_internal
my_openat_done:
ret                   // Return from function
.cfi_endproc
.size my_openat, .-my_openat

.text
.globl my_read
.type my_read, %function
        my_read:
.cfi_startproc
        mov x8, x0            // Move syscall number into x8
mov x0, x1            // Move first argument into x0
mov x1, x2            // Move second argument into x1
mov x2, x3            // Move third argument into x2
mov x3, x4            // Move fourth argument into x3
mov x4, x5            // Move fifth argument into x4
mov x5, x6            // Move sixth argument into x5
svc #0                // Supervisor Call
cmp x0, #0            // Compare return value with 0 (success)
bge my_read_done      // If >= 0, syscall succeeded
neg x0, x0            // Negate x0 to get error code
//b __set_errno_internal
my_read_done:
ret                   // Return from function
.cfi_endproc
.size my_read, .-my_read

#elif defined(__arm__)   // ARM32 architecture (ARMv7-A)

.text
.globl my_openat
.type my_openat, %function
my_openat:
.cfi_startproc
    mov r7, #__NR_openat  // Move syscall number into r7
    swi #0                // Software Interrupt
    cmp r0, #0            // Compare return value with 0 (success)
    bge my_openat_done    // If >= 0, syscall succeeded
    neg r0, r0            // Negate r0 to get error code
//    b __set_errno_internal
my_openat_done:
    bx lr                 // Return from function
.cfi_endproc
.size my_openat, .-my_openat

.text
.globl my_read
.type my_read, %function
my_read:
.cfi_startproc
    mov r7, #__NR_read    // Move syscall number into r7
    swi #0                // Software Interrupt
    cmp r0, #0            // Compare return value with 0 (success)
    bge my_read_done      // If >= 0, syscall succeeded
    neg r0, r0            // Negate r0 to get error code
//    b __set_errno_internal
my_read_done:
    bx lr                 // Return from function
.cfi_endproc
.size my_read, .-my_read


#endif
