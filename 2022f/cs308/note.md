# Lec2  Image formation

## 2.1  Geometric primitives and transformations

- 2D points
  - $\mathbf{x} = (x, y) ∈ R^2$  or  $\mathbf{x}=\left[\begin{array}{cc}x\\y\end{array}\right]$
  - *homogeneous coordinates (齐次坐标)*  $\widetilde{\mathbf{x}}=\left(\widetilde x, \widetilde y, \widetilde w\right) \in \mathbf P^2$
    - w.r.t. $\mathbf P^2 = \mathbf R^3 - \left(0,0,0\right)$   `2D projective space`
    - vectors that differ only by scale are considered to be equivalent
  - *augmented vector*  $\overline{\mathbf x}=\left(x,y,1\right)$
    - a homogeneous vector $\widetilde{\mathbf{x}}$ can be converted back into an inhomogeneous vector $\mathbf{x}$ by dividing through by the last element $\widetilde w$, i.e. $\widetilde{\mathbf{x}}=\widetilde{w}\overline{\mathbf x}$ 
    - homogeneous points whose last element is $\widetilde{w}=0$ are called `ideal points` or `points at inﬁnity` and *do not have an equivalent inhomogeneous representation*
  
- 2D lines
  - $\overline{\mathbf x}\cdot\widetilde{\mathbf l}=ax+by+c=0$,  w.r.t.  $\widetilde{\mathbf l}=\left(a,b,c\right)$
  - normalize it s.t. $\mathbf l=(\hat n_x,\hat n_y, d)=(\hat{\mathbf n},d)$  denoting `n` as direction and `d` as distance, with $\left\Vert\hat{\mathbf n}\right\Vert=1$
    > <img src="note.assets/Screen%20Shot%202022-09-14%20at%2017.25.15.png" alt="Screen Shot 2022-09-14 at 14.21.13" style="zoom:30%;" />
    > $\hat{\mathbf n}=(\cos \theta, \sin \theta)$
  - intersection of two lines:  $\widetilde{\mathbf x}=\widetilde{\mathbf l}_1 \times \widetilde{\mathbf l}_2$
  - the line joining two points can be repr as:  $\widetilde{\mathbf l}=\widetilde{\mathbf x}_1 \times \widetilde{\mathbf x}_2$

- 3D points
  - $\mathbf{x} = (x, y, z) ∈ R^3$  or   $\widetilde{\mathbf{x}}=\left(\widetilde x, \widetilde y, \widetilde z, \widetilde w\right) \in \mathbf P^3$ 
  - $\overline{\mathbf x}=\left(x,y,z,1\right)$  and   $\widetilde{\mathbf{x}}=\widetilde{w}\overline{\mathbf x}$ 

- 3D planes
  - $\overline{\mathbf x}\cdot\widetilde{\mathbf m}=ax+by+cz+d=0$,  w.r.t.  $\widetilde{m}=(a,b,c,d)$,  norm $m=(\hat{\mathbf n},d)=(\cos\theta \cos\phi, \sin\theta \cos\phi, \sin\phi, d)$
- 3D lines
  - use two points on the line, `p` `q` to repr:  $\mathbf r=(1-\lambda) p+\lambda q$
    > <img src="note.assets/Screen%20Shot%202022-09-14%20at%2017.23.55.png" alt="Screen Shot 2022-09-14 at 16.52.14" style="zoom:30%;" />
    > If we restrict $0 \le λ \le 1$, we get the *line segment* joining `p` and `q`
    
  - $\widetilde{\mathbf r}=\mu \widetilde p+\lambda \widetilde q$



### 2.1.1  2D transformations

<img src="note.assets/Screen%20Shot%202022-09-14%20at%2017.23.20.png" alt="Screen Shot 2022-09-14 at 17.23.20" style="zoom:40%;" />

<img src="note.assets/Screen%20Shot%202022-09-14%20at%2017.23.02.png" alt="Screen Shot 2022-09-14 at 17.23.02" style="zoom:30%;" />

- Translation 平移
  - $\mathbf x^{\prime}=\mathbf x+\mathbf t=\left[\begin{array}{cc}\mathbf I&\mathbf t\end{array}\right]\overline{\mathbf x}$   aka   $\left[\begin{array}{c}x+t_x\\y+t_y\\1\end{array}\right]=\left[\begin{array}{ccc}1&0&t_x\\0&1&t_y\end{array}\right]\left[\begin{array}{c}x\\y\\1\end{array}\right]$
  - $\overline{\mathbf x}^{\prime}=\left[\begin{array}{cc}I&t\\\mathbf 0^T&1\end{array}\right]\overline{\mathbf x}$

- Rotation + translation (2D rigid body motion / 2D Euclidean transformation)
  - $\mathbf x^{\prime}=\mathbf R \mathbf x+\mathbf t=\left[\begin{array}{cc}\mathbf R&\mathbf t\end{array}\right]\overline{\mathbf x}$   where   $\mathbf R=\left[\begin{array}{cc}\cos\theta&-\sin\theta\\\sin\theta&\cos\theta\end{array}\right]$ 

- Scaled rotation (similarity transform)
  - $\mathbf x^{\prime}=s\mathbf R \mathbf x+\mathbf t=\left[\begin{array}{cc}s\mathbf R&\mathbf t\end{array}\right]\overline{\mathbf x}$



### 2.1.2  3D transformations  &  3D $\to$ 2D Projections

<img src="note.assets/Screen%20Shot%202022-09-14%20at%2017.22.40.png" alt="Screen Shot 2022-09-14 at 17.22.40" style="zoom:30%;" />

> 3D $\to$ 2D projections: can finish this job by using a linear 3D to 2D `projection matrix` (preserve what info?)

- Orthography
  - $\mathbf x=\left[\begin{array}{cc}\mathbf I_2 & \mathbf 0\end{array}\right]\mathbf p$   where `x` is 2d point and `p` is 3d point
  - $\widetilde{\mathbf x}=\left[\begin{array}{cccc}1&0&0&0\\0&1&0&0\\0&0&0&0\\0&0&0&1\end{array}\right]\widetilde{\mathbf p}$
  - Scaled orthography: First project the world points onto a **local fronto-parallel image plane**, Then scale this image using regular perspective projection: $\mathbf x=\left[\begin{array}{cc}s\mathbf I_2 & \mathbf 0\end{array}\right]\mathbf p$

- ***Perspective*** 透视变换
  - $\widetilde{\mathbf x}=\mathit P_z(p)=\left[\begin{array}{c}x/z\\y/z\\1\end{array}\right]$
  - $\widetilde{\mathbf x}=\left[\begin{array}{cccc}1&0&0&0\\0&1&0&0\\0&0&0&0\\0&0&1&0\end{array}\right]\widetilde{\mathbf p}$    （舍弃 $\mathbf p$ 的 $\omega$ 分量，即投影后不可能恢复该 3D 点到图像的距离）
  - 2-steps: First project 3D points into **normalized device coordinates** in the range, Then rescale these coordinates to **integer pixel coordinates**, a.k.a.
    - $\widetilde{\mathbf x}=\left[\begin{array}{cccc}1&0&0&0\\0&1&0&0\\0&0&-z_\text{far}/z_\text{range}&z_\text{near}z_\text{far}/z_\text{range}\\0&0&1&0\end{array}\right]$     w.r.t $z_\text{range}=z_\text{far}-z_\text{near}$ 
  
  - ref : [visualized frustum diagram](https://webglfundamentals.org/webgl/frustum-diagram.html)



### 2.1.3  Geometry of Image Formation

- Mapping between image and world coordinates
  - Pinhole camera model
    > <img src="note.assets/Screen%20Shot%202022-09-15%20at%2001.23.53.png" alt="Screen Shot 2022-09-15 at 01.23.53" style="zoom:40%;" /><img src="note.assets/Screen%20Shot%202022-09-15%20at%2001.24.24.png" alt="Screen Shot 2022-09-15 at 01.24.24" style="zoom:40%;" />
    
  - Projective geometry
    
    > Lose `length`, `angles`...  Preserve: straight lines are still straight
    
    - Vanishing points and lines
    
  - Projection matrix   $\mathbf x=\mathbf K\left[\begin{array}{cc}\mathbf R&\mathbf t\end{array}\right]\mathbf X$
  
    > <img src="note.assets/Screen%20Shot%202022-09-15%20at%2001.48.20.png" alt="Screen Shot 2022-09-15 at 01.48.20" style="zoom:50%;" />



- Camera Intrinsic (内参数): potential problems caused by the production process, 用一个 7 自由度的校准矩阵 $\mathbf K$ 来修正
  <img src="note.assets/Screen%20Shot%202022-09-15%20at%2001.51.26.png" alt="Screen Shot 2022-09-15 at 01.51.26" style="zoom:50%;" /><br><img src="note.assets/Screen%20Shot%202022-09-15%20at%2001.53.16.png" alt="Screen Shot 2022-09-15 at 01.53.16" style="zoom:50%;" />

- Projection (Camera) matrix

  ><img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.13.35.png" alt="Screen Shot 2022-09-15 at 02.13.35" style="zoom:50%;" />

  - 先对相机内、外提出五个假设来简化：
    - Intrinsic Assumptions
      - Unit aspect ratio
      - Optical center at (0,0)
      - No skew *偏斜*
    - Extrinsic Assumptions
      - No rotation
      - Camera at (0,0,0)

    1. 此时最简化版的 matrix 为：$\mathbf x=\mathbf K\underbrace{\left[\begin{array}{cc}\mathbf I&\mathbf 0\end{array}\right]}_\text{perspective}\mathbf X$  aka.  $w\left[\begin{array}{c}u\\v\\1\end{array}\right]=\left[\begin{array}{c}f&0&0&0\\0&f&0&0\\0&0&1&0\end{array}\right]\left[\begin{array}{c}x\\y\\z\\1\end{array}\right]$
    1. remove assumption: *Optical center at (0,0)*  <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.19.52.png" alt="Screen Shot 2022-09-15 at 02.19.52" style="zoom:55%;" />
    1. remove assumption: *Unit aspect ratio*   <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.20.59.png" alt="Screen Shot 2022-09-15 at 02.20.59" style="zoom:55%;" />
    1. remove assumption: *No skew*   <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.21.55.png" alt="Screen Shot 2022-09-15 at 02.21.55" style="zoom:55%;" />  (where $S$ encodes any possible skew between the sensor axes due to the sensor not being mounted perpendicular to the optical axis)
    1. remove assumption: *Camera at (0,0,0)*   <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.23.37.png" alt="Screen Shot 2022-09-15 at 02.23.37" style="zoom:50%;" />
       <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.23.56.png" alt="Screen Shot 2022-09-15 at 02.23.56" style="zoom:50%;" />
    1. allow camera rotation    <img src="note.assets/Screen%20Shot%202022-09-15%20at%2002.25.02.png" alt="Screen Shot 2022-09-15 at 02.25.02" style="zoom:30%;" />

- **Vanishing point** = Projection from infinity

  - See [lec2-image.formation.pdf](lec/lec2-image.formation.pdf)




## 2.2  Digital Camera

- Image sensing pipeline
  <img src="note.assets/Screen%20Shot%202022-09-16%20at%2015.59.04.png" alt="Screen Shot 2022-09-15 at 02.29.00" style="zoom:30%;" />

> A digital camera replaces film with a ***sensor array***:  2 common types
>
> - Charge Coupled Device (CCD)  *expensive*
> - CMOS

- Sampling and Quantization
  - ***Shannon’s Sampling Theorem***   $f_s\ge 2f_\text{max}$

- Color Spaces
- Color Filter Arrays





# Let3 Linear Filter

## 3.1  Point (Pixel) Operators

> Image can be considered  as a function in two-dimensional space: (x, y) -> (r, g, b)...

<img src="note.assets/Screen%20Shot%202022-09-21%20at%2014.37.56.png" alt="Screen Shot 2022-09-21 at 14.37.56" style="zoom:30%;" />