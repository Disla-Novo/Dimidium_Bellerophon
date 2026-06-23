# Known Limitations & Workarounds

## Current Limitations

### Firmware Support
- **Supported**: Klipper, Marlin
- **Planned**: RepRap firmware, Smoothieware
- Currently, only these two adapters are implemented. New firmware targets require extending `GCodeVisitor` (see [CONTRIBUTING.md](CONTRIBUTING.md#adding-firmware-support)).

### Macro & Loop Constraints
- **Variable scope**: All variables are global within a macro. No local scoping.
- **Macro recursion**: Calling a macro from itself is not supported.

### Geometry & Hardware
- **Circular interpolation**: G-code generation uses linear moves only. Arcs must be decomposed manually.
- **Coordinate limits**: All moves are validated against `PrinterProfile` bounds at compile time. Out-of-bounds moves throw errors (intentional safety feature).

### Gravity Hub (Beta)
- **Single network only**: No WAN/cloud routing yet. All connected printers must be on same local network.
- **No job queuing**: Jobs execute immediately. Sequential printing requires external orchestration.
- **No failure recovery**: If a print fails mid-job, manual intervention is required to resume.

---

## Common Error Messages & Solutions

### "ERROR: 'i' iterator is only allowed inside Brepeat loops"
**Cause**: You used the loop variable `i` outside a `Brepeat` block.

**Solution**: Move the expression using `i` inside a `Brepeat` block.
```bellerophon
M.title "Wrong"
Absolute
MoveTo x=i  # ERROR! 'i' is undefined here
M.end

M.title "Correct"
Absolute
Brepeat 10
  MoveTo x=i*2
end
M.end
```

### "X position 250 out of bounds (0-200)"
**Cause**: Your move exceeds the printer's axis limits.

**Solution**: Check your `PrinterProfile` settings in the CFG Generator and verify coordinate values.
```bellerophon
M.title "Example"
Absolute
SetSpeed = 3000
; Assuming printer max is X=200
MoveTo x=250  ; ERROR! Exceeds limit
MoveTo x=150  ; OK
M.end
```

### "Compilation hangs / takes too long"
**Cause**: Very large `Brepeat` counts or deeply nested loops.

**Solution**: 
- Check repeat counts (avoid > 10,000 iterations for complex bodies)
- Profile with smaller repeat counts first:
```bellerophon
M.title "Test"
Absolute
SetSpeed = 3000
Brepeat 100  ; Start here, test output
  MoveTo x=10 y=10
end
M.end

; Once working, increase to 1000, 10000, etc.
```

### "Memory Paging Failed"
**Cause**: G-code output exceeded available system memory. Compiler switched to disk paging and encountered an I/O error.

**Solution**:
- Free up disk space (paging uses system temp directory)
- Reduce repeat counts or macro complexity
- Split into multiple macros and call them sequentially

---

## Workarounds for Current Limitations

### Deep Loop Nesting
**Problem**: Deeply nested loops can generate large G-code outputs and slow compilation.

**Workaround**: Factor into multiple macros and call them:
```bellerophon
M.title "Outer"
Absolute
Brepeat 10
  M.call "Middle"
end
M.end

M.title "Middle"
Absolute
Brepeat 5
  M.call "Inner"
end
M.end

M.title "Inner"
Absolute
Brepeat 3
  MoveTo z+=0.1
end
M.end
```

### Variable Scoping
**Problem**: You need temporary variables that don't affect other macros.

**Workaround**: Use naming conventions to avoid collisions:
```bellerophon
M.title "Square A"
Absolute
tempA_x = 10
tempA_y = 20
MoveTo x=tempA_x y=tempA_y
M.end

M.title "Square B"
Absolute
tempB_x = 30  # Different namespace, won't collide
tempB_y = 40
MoveTo x=tempB_x y=tempB_y
M.end
```

### Circular Paths
**Problem**: You want to draw a circle or arc.

**Workaround**: Approximate with linear segments using math:
```bellerophon
M.title "Circular Path"
Absolute
SetSpeed = 3000
radius = 20
numSteps = 36  # 36 * 10° = 360°
Brepeat numSteps
  angle = i * 10 * 3.14159 / 180  # Convert degrees to radians
  xPos = radius * cos(angle)
  yPos = radius * sin(angle)
  MoveTo x=xPos y=yPos
end
M.end
```

---

## Planned Improvements

- [ ] Local variable scoping (vs global-only)
- [ ] Macro recursion safety checks 
- [ ] RepRap firmware support
- [ ] Gravity Hub job queuing 
- [ ] Multi-network WAN support 

---

## Found a bug? 

See [CONTRIBUTING.md](CONTRIBUTING.md#getting-help) for how to report issues and ask for help.
