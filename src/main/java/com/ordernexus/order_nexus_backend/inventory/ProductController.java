package com.ordernexus.order_nexus_backend.inventory;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    private final ProductService productService;

    // Constructor injection (no Lombok)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ ADD PRODUCT
    @PostMapping(consumes = { "multipart/form-data" })
    public Product add(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image)
            throws java.io.IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads");
            if (!java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
            }
            java.nio.file.Files.copy(image.getInputStream(), path.resolve(fileName),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            imageUrl = "/uploads/" + fileName;
        }

        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setPrice(price);
        request.setQuantity(quantity);
        request.setImageUrl(imageUrl);
        return productService.addProduct(request);
    }

    // ✅ GET ALL PRODUCTS
    @GetMapping
    public List<Product> all() {
        return productService.getAll();
    }

    // ✅ GET PRODUCT BY ID
    @GetMapping("/{id}")
    public Product find(@PathVariable Long id) {
        return productService.getById(id);
    }

    // ✅ DELETE PRODUCT
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // ✅ UPDATE PRODUCT (PUT)
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public Product update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image)
            throws java.io.IOException {

        Product existingProduct = productService.getById(id);
        String imageUrl = existingProduct.getImageUrl();

        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads");
            if (!java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
            }
            java.nio.file.Files.copy(image.getInputStream(), path.resolve(fileName),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            imageUrl = "/uploads/" + fileName;
        }

        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setPrice(price);
        request.setQuantity(quantity);
        request.setImageUrl(imageUrl);

        return productService.updateProductFull(id, request);
    }

    // ✅ ADD STOCK (PUT)
    @PutMapping("/{id}/stock")
    public Product addStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request) {
        System.out.println("🔥 ADD STOCK HIT: id=" + id + ", qty=" + request.getQuantity());
        return productService.updateProduct(id, request.getQuantity());
    }
}
